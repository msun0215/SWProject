package com.example.BoardDBRestAPIBySpring.domain;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
@Getter
@RequiredArgsConstructor
public enum Role {
    ADMIN("ROLE_ADMIN", "관리자"),
    MANAGER("ROLE_MANAGER", "매니저"),
    USER("ROLE_USER", "일반 사용자");

    private final String key;
    private final String title;
}
*/

@Data   // GETTER & SETTER
@Entity
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // MYSQL에서 AutoIncrement
    private long roleID;
    private String roleName;
    // camelCase error로 인한 _제거


    @Builder
    public Role(long roleID, String roleName) {
        this.roleID = roleID;
        this.roleName = roleName;
    }

    @Builder
    public Role(long roleID){
        this.roleID=roleID;
    }

    // ENUM으로 안하고 ,로 split하여 ROLE을 입력 -> 그걸 parsing
    public List<String> getRoleList(){
        if(this.roleName.length()>0){
            return Arrays.asList(this.roleName.split(","));
        }
        return new ArrayList<>();
    }

}