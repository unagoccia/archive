package casino

class Stakes {

    /**
     * 回転数
     */
    int continuousSlips

    /**
     * 賭け金
     */
    float stakes

    static constraints = {
        continuousSlips nullable: false, blank: false, unique: true
        stakes nullable: false, blank: false
    }
}
