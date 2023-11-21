package com.example.BoardDBRestAPIBySpring.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data   // GETTER & SETTER
@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // MYSQL에서 AutoIncrement
    private long roleId;
    private String roleName;
    // camelCase error로 인한 _제거


    // ENUM으로 안하고 ,로 split하여 ROLE을 입력 -> 그걸 parsing
    public List<String> getRoleList(){
        if(this.roleName.length()>0){
            return Arrays.asList(this.roleName.split(","));
        }
        return new ArrayList<>();
    }
}