package com.example.BoardDBRestAPIBySpring.repository;

import com.example.BoardDBRestAPIBySpring.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;


// CRUD 함수를 JpaRepository가 가지고 있다.
// @Repository라는 annotation이 없어도 IoC가 된다.
// JpaRepository를 상속했기 때문에
public interface RoleRepository extends JpaRepository<Role, Long> {
    // findBy는 규칙=>Username문법
    // select * from ROLE where roleID=?

    public Role findByRoleID(long roleID);    // JPA Query Method
    //public String findRoleNameByRoldID(long roleID);
}
