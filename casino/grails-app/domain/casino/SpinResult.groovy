package casino

class SpinResult {

    /**
     * ヒットした数
     */
    int hitNumber

    /**
     * 前回の当たりからの回転数
     */
    int spinNumberFromLastHit

    static constraints = {
        hitNumber nullable: false, blank: false
        spinNumberFromLastHit nullable: false, blank: false
    }
}
