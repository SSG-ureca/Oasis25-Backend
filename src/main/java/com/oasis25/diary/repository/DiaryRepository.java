package com.oasis25.diary.repository;

import com.oasis25.diary.entity.Diary;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

        @Query("select d from Diary d where d.user.id = :userId and d.diaryDate = :diaryDate")
        Optional<Diary> findByUserIdAndDiaryDate(
                        @Param("userId") Long userId,
                        @Param("diaryDate") LocalDate diaryDate);

        @Query("select d from Diary d where d.id = :id and d.user.id = :userId")
        Optional<Diary> findByIdAndUserId(
                        @Param("id") Long id,
                        @Param("userId") Long userId);

        @Query("select case when count(d) > 0 then true else false end from Diary d where d.user.id = :userId and d.diaryDate = :diaryDate")
        boolean existsByUserIdAndDiaryDate(
                        @Param("userId") Long userId,
                        @Param("diaryDate") LocalDate diaryDate);

        @Query("select d from Diary d where d.user.id = :userId and d.diaryDate >= :startDate and d.diaryDate < :endDate order by d.diaryDate asc")
        List<Diary> findByUserIdAndDiaryDateBetween(
                        @Param("userId") Long userId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);
}
