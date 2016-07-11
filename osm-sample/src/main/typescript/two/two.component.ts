import {Component} from '@angular/core';
import {Http, Response} from '@angular/http';
import {htmlTemplate} from './two.html';

declare var baseUrl: any;

@Component({
    selector: 'two',
    template: htmlTemplate
})
export class TwoComponent {
    http: Http;
    baseUrl: string;
    data: Object;

    constructor(http: Http) {
        this.http = http;
        this.baseUrl = baseUrl;

        this.getData();
    }

    getData() {
        this.http.get(this.baseUrl + '/data/two')
            .subscribe((res: Response) => {
                this.data = res.json()['data'];
            })
    }
}
