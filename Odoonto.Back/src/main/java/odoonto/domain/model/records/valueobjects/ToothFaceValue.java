package odoonto.domain.model.records.valueobjects;

public enum ToothFaceValue {
    MESIAL("M", "Mesial"),
    DISTAL("D", "Distal"), 
    BUCCAL("B", "Buccal"),
    LINGUAL("L", "Lingual"),
    OCCLUSAL("O", "Occlusal"),
    INCISAL("I", "Incisal");

    private final String code;
    private final String displayName;

    ToothFaceValue(final String code, final String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ToothFaceValue fromCode(final String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Tooth face code cannot be null or empty");
        }
        
        for (ToothFaceValue face : values()) {
            if (face.code.equalsIgnoreCase(code.trim())) {
                return face;
            }
        }
        
        throw new IllegalArgumentException("Invalid tooth face code: " + code);
    }
} 