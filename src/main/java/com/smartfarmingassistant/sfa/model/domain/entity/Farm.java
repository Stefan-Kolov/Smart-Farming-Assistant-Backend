package com.smartfarmingassistant.sfa.model.domain.entity;

import com.smartfarmingassistant.sfa.model.domain.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "farms")
public class Farm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank
    @Column(nullable = false, length = 255)
    private String location;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "farm", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Crop> crops = new ArrayList<>();

    @OneToMany(mappedBy = "farm", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Recommendation> recommendations = new ArrayList<>();

    @OneToMany(mappedBy = "farm", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WeatherRecord> weatherRecords = new ArrayList<>();

    public Farm() {
    }

    public Farm(String name, String location, User user) {
        this.name = name;
        this.location = location;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Crop> getCrops() {
        return crops;
    }

    public void setCrops(List<Crop> crops) {
        this.crops = crops;
    }

    public List<Recommendation> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<Recommendation> recommendations) {
        this.recommendations = recommendations;
    }

    public List<WeatherRecord> getWeatherRecords() {
        return weatherRecords;
    }

    public void setWeatherRecords(List<WeatherRecord> weatherRecords) {
        this.weatherRecords = weatherRecords;
    }

    public void addCrop(Crop crop) {
        crops.add(crop);
        crop.setFarm(this);
    }

    public void removeCrop(Crop crop) {
        crops.remove(crop);
        crop.setFarm(null);
    }

    public void addRecommendation(Recommendation recommendation) {
        recommendations.add(recommendation);
        recommendation.setFarm(this);
    }

    public void removeRecommendation(Recommendation recommendation) {
        recommendations.remove(recommendation);
        recommendation.setFarm(null);
    }

    public void addWeatherRecord(WeatherRecord weatherRecord) {
        weatherRecords.add(weatherRecord);
        weatherRecord.setFarm(this);
    }

    public void removeWeatherRecord(WeatherRecord weatherRecord) {
        weatherRecords.remove(weatherRecord);
        weatherRecord.setFarm(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Farm)) return false;
        Farm farm = (Farm) o;
        return id != null && Objects.equals(id, farm.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}