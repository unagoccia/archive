package casino

class SpinResult {

    /**
     * ヒットした数
     */
    int hitNumber

    /**
     * 前回の当たりからの回転数
     */
    int recentHit

    static constraints = {
        hitNumber nullable: false, blank: false
        recentHit nullable: false, blank: false
    }

    static mapping = {
      hitNumber index: true
    }
}
