package com.novahire.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateAvatarRequest {
    /** Full data-URL: "data:image/png;base64,iVBOR..." — max ~2MB */
    @NotBlank(message = "Avatar data is required")
    private String avatarBase64;
}
