import {Component} from '@angular/core';
import {Routes, ROUTER_DIRECTIVES} from '@angular/router';
import {htmlTemplate} from './app.html';
import {HomeComponent} from './home'
import {OneComponent} from './one';
import {TwoComponent} from './two';

@Component({
    selector: 'ng2starter',
    template: htmlTemplate,
    directives: [ROUTER_DIRECTIVES]
})
@Routes([
    { path: '/home', component: HomeComponent},
    { path: '/one', component: OneComponent },
    { path: '/two', component: TwoComponent }
])
export class AppComponent {
    constructor() {
    }
}
