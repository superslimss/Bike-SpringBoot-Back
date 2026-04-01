package com.bike.controller;

import com.bike.dto.LatLngDTO;
import com.bike.dto.ParkingAreaDTO;
import com.bike.entity.ParkingArea;
import com.bike.repository.ParkingAreaRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/parking-areas")
@CrossOrigin // 你前端是小程序本地调试的话建议先开着
public class ParkingAreaController {

    private final ParkingAreaRepository repo;
    private final ObjectMapper om = new ObjectMapper();

    public ParkingAreaController(ParkingAreaRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/list")
    public List<ParkingAreaDTO> list() throws Exception {
        List<ParkingArea> all = repo.findAll();
        List<ParkingAreaDTO> out = new ArrayList<>();

        for (ParkingArea a : all) {
            List<Map<String, Object>> pts =
                    om.readValue(a.getPolygonJson(), new TypeReference<List<Map<String, Object>>>() {});

            List<LatLngDTO> dtoPts = new ArrayList<>();
            for (Map<String, Object> p : pts) {
                double lat = ((Number) p.get("lat")).doubleValue();
                double lng = ((Number) p.get("lng")).doubleValue();
                dtoPts.add(new LatLngDTO(lat, lng));
            }

            ParkingAreaDTO dto = new ParkingAreaDTO();
            dto.setId(a.getId());
            dto.setName(a.getName());
            dto.setPoints(dtoPts);
            out.add(dto);
        }
        return out;
    }

    @PostMapping("/create")
    public ParkingArea create(@RequestBody ParkingAreaDTO req) throws Exception {
        if (req.getName() == null || req.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("name 不能为空");
        }
        if (req.getPoints() == null || req.getPoints().size() < 3) {
            throw new IllegalArgumentException("polygon 至少需要 3 个点");
        }

        double minLat = Double.POSITIVE_INFINITY, maxLat = Double.NEGATIVE_INFINITY;
        double minLng = Double.POSITIVE_INFINITY, maxLng = Double.NEGATIVE_INFINITY;

        for (LatLngDTO p : req.getPoints()) {
            minLat = Math.min(minLat, p.getLat());
            maxLat = Math.max(maxLat, p.getLat());
            minLng = Math.min(minLng, p.getLng());
            maxLng = Math.max(maxLng, p.getLng());
        }

        ParkingArea a = new ParkingArea();
        a.setName(req.getName());
        a.setPolygonJson(om.writeValueAsString(req.getPoints()));
        a.setMinLat(minLat);
        a.setMaxLat(maxLat);
        a.setMinLng(minLng);
        a.setMaxLng(maxLng);

        return repo.save(a);
    }
}