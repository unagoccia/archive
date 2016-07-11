
/// <reference path="../../../../typings/globals/leaflet/index.d.ts" />

import {Component} from '@angular/core';
// import {MapService} from '../services/map.service';
import {htmlTemplate} from './map.html';

@Component({
  selector: 'map-component',
  template: htmlTemplate
})
export class MapComponent {
    private title;
    // private mapService: MapService;

    constructor() {
        this.title = 'I\'m a nested component';
    }

    ngOnInit() {

        var OpenStreetMap = new L.TileLayer("http://{s}.tile.osm.org/{z}/{x}/{y}.png", {
            attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>, Tiles courtesy of <a href="http://hot.openstreetmap.org/" target="_blank">Humanitarian OpenStreetMap Team</a>'
        })
        console.log(OpenStreetMap);
        var map = new L.Map('map', {
          zoomControl: false,
          center: new L.LatLng(40.731253, -73.996139),
          zoom: 12,
          minZoom: 4,
          maxZoom: 19,
          layers: [OpenStreetMap]
        });

        // L.control.zoom({ position: 'topright' }).addTo(map);
        // L.control.layers(this.mapService.baseMaps).addTo(map);
        // L.control.scale().addTo(map);
        //
        // this.mapService.map = map;

        // this.geocoder.getCurrentLocation()
        // .subscribe(
        //     location => map.panTo([location.latitude, location.longitude]),
        //     err => console.error(err)
        // );
    }

}
