package com.oasis25.pomodoro.repository;

import com.oasis25.pomodoro.entity.FocusCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FocusCategoryRepository extends JpaRepository<FocusCategory, Long> {

    @Query("select c from FocusCategory c where c.user.id = :userId order by c.createdAt desc")
    List<FocusCategory> findByUserId(@Param("userId") Long userId);

    @Query("select c from FocusCategory c where c.id = :id and c.user.id = :userId")
    Optional<FocusCategory> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Query("select case when count(c) > 0 then true else false end from FocusCategory c where c.user.id = :userId and c.name = :name")
    boolean existsByUserIdAndName(@Param("userId") Long userId, @Param("name") String name);
}
