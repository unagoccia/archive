package ng2starter

import grails.converters.JSON

class DataController {
    def one() {
        Map data = [data: 'Sample Content One']
        render data as JSON
    }

    def two() {
        Map data = [data: 'Sample Content Two']
        render data as JSON
    }
}
