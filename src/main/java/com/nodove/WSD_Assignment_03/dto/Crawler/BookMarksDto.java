package com.nodove.WSD_Assignment_03.dto.Crawler;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookMarksDto {

    boolean isDeleteRequest; // true if delete request, false if add request
}
