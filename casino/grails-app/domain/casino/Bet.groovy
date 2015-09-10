package casino

class Bet {

    /**
     * ベット数字
     */
    int betNumber

    /**
     * ステータス
     */
    String status

    /**
     * 結果
     */
    SpinResult spinResult

    /**
     * 利益
     */
    Profit profit

    // ==========
    // ステータスの種類
    /**
     * ベット候補
     */
    public static final String STATUS_BET_CANDIDATE = "STATUS_BET_CANDIDATE"

    /**
     * ベット
     */
    public static final String STATUS_BET = "STATUS_BET"

    /**
     * ベットなし
     */
    public static final String STATUS_NO_BET = "STATUS_NO_BET"

    static constraints = {
        betNumber nullable: false, blank: false
        status nullable: false, blank: false
        spinResult unique: true
        profit unique: true
    }
}
