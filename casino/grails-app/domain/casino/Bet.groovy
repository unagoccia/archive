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
    public static final String STATUS_READY = "READY"

    /**
     * ベット
     */
    public static final String STATUS_BET = "BET"

    /**
     * 一時待ち　※ベット中に100に到達した場合
     */
    public static final String STATUS_WAIT = "WAIT"

    /**
     * 負け
     */
    public static final String STATUS_WIN = "WIN"

    /**
     * 負け
     */
    public static final String STATUS_LOSE = "LOSE"

    /**
     * ベットなし
     */
    public static final String STATUS_NO_BET = "NO_BET"

    static constraints = {
        betNumber nullable: false, blank: false
        status nullable: false, blank: false
        spinResult unique: true, nullable: true, blank: true
        profit unique: true, nullable: true, blank: true
    }

    static mapping = {
      betNumber index: true
    }
}
