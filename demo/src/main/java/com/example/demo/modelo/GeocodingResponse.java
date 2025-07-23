package com.example.demo.modelo;

import java.util.List;

public class GeocodingResponse {
    private List<LatLng> data;

    public List<LatLng> getData() {
        return data;
    }

    public void setData(List<LatLng> data) {
        this.data = data;
    }
}