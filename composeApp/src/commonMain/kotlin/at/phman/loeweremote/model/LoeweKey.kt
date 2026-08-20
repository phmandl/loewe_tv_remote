package at.phman.loeweremote.model

/**
 * KeyCodes for Loewe bild TV (Chassis SL420, SL3xx, SL5xx, Loewe OS).
 * Official Reference: LOEWE TV remote API specification (Doc Rev 1.0.47, Loewe Technologies GmbH, Kronach).
 */
enum class LoeweKey(
    val code: Int,
    val label: String,
    val description: String = "",
    val alphabet: String = "l2700"
) {
    // Power & Audio (Alphabet: l2700)
    POWER(12, "Power", "Standby / Power Toggle (ON_OFF)"),
    TV_ON(22, "Power On", "Discrete TV On"),
    TV_OFF(25, "Power Off", "Discrete TV Off"),
    MUTE(13, "Mute", "Audio Mute Toggle"),
    VOLUME_UP(21, "Vol +", "Volume Up (VOL_PL)"),
    VOLUME_DOWN(20, "Vol -", "Volume Down (VOL_MI)"),

    // Channel / Program (Alphabet: l2700)
    PROGRAM_UP(24, "P +", "Program Up (PROG_PL)"),
    PROGRAM_DOWN(23, "P -", "Program Down (PROG_MI)"),

    // Navigation & System (Alphabet: l2700)
    HOME(49, "Home", "Home / Media Menu (MEDIA)"),
    MENU(11, "Menu", "System Settings Menu (MENU)"),
    BACK(65, "Back", "Back / Return (BACK)"),
    END(63, "End", "Exit / End (END)"),
    INFO(79, "Info", "Program & Media Info (INFO)"),
    EPG(15, "EPG", "Electronic Program Guide (EPG)"),
    TEXT(60, "Text", "Teletext (TTX)"),
    PIP(10, "PIP", "Picture in Picture (PIP)"),
    RADIO(53, "Radio", "Radio Mode (RADIO)"),
    SOUND(64, "Sound", "Sound Menu (SOUND)"),
    ASPECT(90, "Aspect", "Aspect Ratio (ASPECT)"),
    DR_ARCHIVE(92, "DR+", "DR+ Archive (DR_ARCHIVE)"),

    // Direct Inputs (Alphabet: l2700)
    HDMI1(119, "HDMI 1", "Direct Input HDMI 1"),
    HDMI2(121, "HDMI 2", "Direct Input HDMI 2"),
    HDMI3(122, "HDMI 3", "Direct Input HDMI 3"),
    HDMI4(123, "HDMI 4", "Direct Input HDMI 4"),

    // D-Pad (Alphabet: l2700)
    UP(32, "Up", "Navigate Up (UP)"),
    DOWN(33, "Down", "Navigate Down (DOWN)"),
    LEFT(17, "Left", "Navigate Left (LEFT)"),
    RIGHT(16, "Right", "Navigate Right (RIGHT)"),
    OK(38, "OK", "Select / Confirm (OK)"),

    // Color Keys (Alphabet: l2700)
    RED(27, "Red", "Red Interactive Function (RED)"),
    GREEN(26, "Green", "Green Interactive Function (GREEN)"),
    YELLOW(43, "Yellow", "Yellow Interactive Function (YELLOW)"),
    BLUE(40, "Blue", "Blue Interactive Function (BLUE)"),

    // Numeric Keys 0 - 9 (Alphabet: l2700)
    NUM_0(0, "0", "Digit 0"),
    NUM_1(1, "1", "Digit 1"),
    NUM_2(2, "2", "Digit 2"),
    NUM_3(3, "3", "Digit 3"),
    NUM_4(4, "4", "Digit 4"),
    NUM_5(5, "5", "Digit 5"),
    NUM_6(6, "6", "Digit 6"),
    NUM_7(7, "7", "Digit 7"),
    NUM_8(8, "8", "Digit 8"),
    NUM_9(9, "9", "Digit 9"),

    // DR+ / Media Playback Keys (Alphabet: l2700-hdr - Sec 9.4.1.2)
    PLAY(53, "Play", "Play / Resume (HDR_PLAY)", alphabet = "l2700-hdr"),
    PAUSE(41, "Pause", "Pause Playback (HDR_PAUSE)", alphabet = "l2700-hdr"),
    STOP(54, "Stop", "Stop Playback (HDR_STOP)", alphabet = "l2700-hdr"),
    REWIND(50, "Rewind", "Rewind (HDR_REW)", alphabet = "l2700-hdr"),
    FAST_FORWARD(52, "Fast Fwd", "Fast Forward (HDR_FF)", alphabet = "l2700-hdr"),
    RECORD(55, "Record", "Start Recording (HDR_REC)", alphabet = "l2700-hdr");

    companion object {
        fun fromDigit(digit: Int): LoeweKey? = when (digit) {
            0 -> NUM_0
            1 -> NUM_1
            2 -> NUM_2
            3 -> NUM_3
            4 -> NUM_4
            5 -> NUM_5
            6 -> NUM_6
            7 -> NUM_7
            8 -> NUM_8
            9 -> NUM_9
            else -> null
        }
    }
}
