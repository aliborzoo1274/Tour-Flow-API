package com.tour.tour.service;

import com.tour.tour.dto.TravelRequest;
import com.tour.tour.dto.TravelResponse;
import com.tour.tour.dto.CancellationRuleResponse;
import com.tour.tour.exception.ResourceNotFoundException;
import com.tour.tour.model.Travel;
import com.tour.tour.repository.TravelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.time.LocalDate;

@Service
@Transactional
public class TravelService {

    private final TravelRepository travelRepository;

    public TravelService(TravelRepository travelRepository) {
        this.travelRepository = travelRepository;
    }

    public TravelResponse createTravel(TravelRequest request) {
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        
        String dirName = generateTravelDirName(request.getName(), request.getStartDate());
        String coverPath = saveImage(request.getCoverImage(), dirName);
        List<String> imagePaths = new ArrayList<>();
        if (request.getImages() != null) {
            for (MultipartFile img : request.getImages()) {
                String path = saveImage(img, dirName);
                if (path != null) imagePaths.add(path);
            }
        }

        Travel travel = new Travel(
                request.getName(),
                request.getCapacity(),
                request.getCost(),
                request.getStartDate(),
                request.getEndDate(),
                request.getCapacity(),
                request.getBoardingPlaces(),
                request.getDescription(),
                coverPath,
                imagePaths
        );
        travel = travelRepository.save(travel);
        return toResponse(travel);
    }

    public TravelResponse getTravelById(Long id) {
        Travel travel = travelRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Travel not found with ID: " + id));
        return toResponse(travel);
    }

    public List<TravelResponse> getAllTravels() {
        return travelRepository.findAll().stream().map(this::toResponse).toList();
    }

    public TravelResponse updateTravel(Long id, TravelRequest request) {
        Travel travel = travelRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Travel not found with ID: " + id));
        
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        int capacityDifference = request.getCapacity() - travel.getCapacity();
        int newRemainedCapacity = travel.getRemainedCapacity() + capacityDifference;

        if (newRemainedCapacity < 0) {
            throw new IllegalStateException("Cannot reduce capacity below the number of registered travelers.");
        }

        travel.setName(request.getName());
        travel.setCapacity(request.getCapacity());
        travel.setCost(request.getCost());
        travel.setStartDate(request.getStartDate());
        travel.setEndDate(request.getEndDate());
        travel.setRemainedCapacity(newRemainedCapacity);
        travel.setBoardingPlaces(request.getBoardingPlaces());
        travel.setDescription(request.getDescription());

        String dirName = generateTravelDirName(request.getName(), request.getStartDate());

        if (request.getCoverImage() != null && !request.getCoverImage().isEmpty()) {
            deleteFileLocally(travel.getCoverImagePath());
            travel.setCoverImagePath(saveImage(request.getCoverImage(), dirName));
        }

        List<String> finalImagePaths = new ArrayList<>();
        if (request.getExistingImages() != null) {
            finalImagePaths.addAll(request.getExistingImages());
        }

        if (travel.getImagePaths() != null) {
            for (String dbPath : travel.getImagePaths()) {
                if (request.getExistingImages() == null || !request.getExistingImages().contains(dbPath)) {
                    deleteFileLocally(dbPath);
                }
            }
        }

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            for (MultipartFile img : request.getImages()) {
                String path = saveImage(img, dirName);
                if (path != null) finalImagePaths.add(path);
            }
        }
        
        travel.setImagePaths(finalImagePaths);
        
        travel = travelRepository.save(travel);
        return toResponse(travel);
    }

    public TravelResponse updateRegistrationStatus(Long id, boolean closed) {
        Travel travel = travelRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Travel not found with ID: " + id));
        travel.setRegistrationClosed(closed);
        travel = travelRepository.save(travel);
        return toResponse(travel);
    }

    public void deleteTravel(Long id) {
        Travel travel = travelRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Travel not found with ID: " + id));
        
        if (travel.getCoverImagePath() != null) {
            deleteFileLocally(travel.getCoverImagePath());
        }
        if (travel.getImagePaths() != null) {
            for (String path : travel.getImagePaths()) {
                deleteFileLocally(path);
            }
        }
        
        try {
            String dirName = generateTravelDirName(travel.getName(), travel.getStartDate());
            Files.deleteIfExists(Paths.get("uploads/travels/" + dirName));
        } catch (IOException e) {
        }

        travelRepository.delete(travel);
    }

    private TravelResponse toResponse(Travel travel) {
        return new TravelResponse(
                travel.getId(),
                travel.getName(),
                travel.getCapacity(),
                travel.getCost(),
                travel.getStartDate(),
                travel.getEndDate(),
                travel.getRemainedCapacity(),
                travel.getBoardingPlaces(),
                travel.isRegistrationClosed(),
                travel.getDescription(),
                travel.getCoverImagePath(),
                travel.getImagePaths(),
                calculateCancellationRules(travel.getStartDate())
        );
    }

    private List<CancellationRuleResponse> calculateCancellationRules(LocalDate startDate) {
        List<CancellationRuleResponse> rules = new ArrayList<>();
        rules.add(new CancellationRuleResponse(null, startDate.minusDays(9), 10));
        rules.add(new CancellationRuleResponse(startDate.minusDays(8), startDate.minusDays(6), 30));
        rules.add(new CancellationRuleResponse(startDate.minusDays(5), startDate.minusDays(3), 60));
        rules.add(new CancellationRuleResponse(startDate.minusDays(2), null, 100));
        return rules;
    }

    private String saveImage(MultipartFile file, String dirName) {
        if (file == null || file.isEmpty()) return null;
        try {
            String uploadDir = "uploads/travels/" + dirName + "/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + extension;
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/travels/" + dirName + "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Could not store the image file.", e);
        }
    }

    private void deleteFileLocally(String urlPath) {
        if (urlPath == null) return;
        try {
            String localPath = urlPath.startsWith("/") ? urlPath.substring(1) : urlPath;
            Files.deleteIfExists(Paths.get(localPath));
        } catch (IOException e) {
        }
    }

    private String generateTravelDirName(String name, LocalDate startDate) {
        String safeName = name.replaceAll("[^\\p{L}\\p{N}.-]", "_");
        return safeName + "_" + startDate.toString();
    }
}
