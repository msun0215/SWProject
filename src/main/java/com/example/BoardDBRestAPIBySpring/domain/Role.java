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
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // MYSQL에서 AutoIncrement
    private long roleID;
    private String roleName;
    // camelCase error로 인한 _제거

//    @OneToMany(mappedBy = "roles", fetch = FetchType.EAGER)
//    private List<Member> members;

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