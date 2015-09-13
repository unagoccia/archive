package casino

class Stakes {

    /**
     * 回転数
     */
    int spinNum

    /**
     * 賭け金
     */
    float stakes

    /**
     * 賭け金(累計)
     */
    float stakesTotal

    /**
     * 利益
     */
    float profit

    static constraints = {
        spinNum nullable: false, blank: false, unique: true
        stakes nullable: false, blank: false
        stakesTotal nullable: true, blank: true
        profit nullable: true, blank: true
    }
}
