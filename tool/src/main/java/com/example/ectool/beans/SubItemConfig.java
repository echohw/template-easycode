package com.example.ectool.beans;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by AMe on 2021-09-22 20:54.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubItemConfig implements Serializable {
    
    private String name;
    private List<Map<String, Object>> elementList;
}
