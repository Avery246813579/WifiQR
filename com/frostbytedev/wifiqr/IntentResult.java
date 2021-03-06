package com.frostbytedev.wifiqr;

public final class IntentResult {
    private final String contents;
    private final String errorCorrectionLevel;
    private final String formatName;
    private final Integer orientation;
    private final byte[] rawBytes;

    IntentResult() {
        this(null, null, null, null, null);
    }

    IntentResult(String contents, String formatName, byte[] rawBytes, Integer orientation, String errorCorrectionLevel) {
        this.contents = contents;
        this.formatName = formatName;
        this.rawBytes = rawBytes;
        this.orientation = orientation;
        this.errorCorrectionLevel = errorCorrectionLevel;
    }

    public String getContents() {
        return this.contents;
    }

    public String getFormatName() {
        return this.formatName;
    }

    public byte[] getRawBytes() {
        return this.rawBytes;
    }

    public Integer getOrientation() {
        return this.orientation;
    }

    public String getErrorCorrectionLevel() {
        return this.errorCorrectionLevel;
    }

    public String toString() {
        StringBuilder dialogText = new StringBuilder(100);
        dialogText.append("Format: ").append(this.formatName).append('\n');
        dialogText.append("Contents: ").append(this.contents).append('\n');
        dialogText.append("Raw bytes: (").append(this.rawBytes == null ? 0 : this.rawBytes.length).append(" bytes)\n");
        dialogText.append("Orientation: ").append(this.orientation).append('\n');
        dialogText.append("EC level: ").append(this.errorCorrectionLevel).append('\n');
        return dialogText.toString();
    }
}
