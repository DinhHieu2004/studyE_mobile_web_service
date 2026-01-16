package com.example.studyE.repository;

import com.example.studyE.entity.Lession;
import com.example.studyE.entity.User;
import com.example.studyE.entity.UserWatchedLesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserWatchedLessonRepository extends JpaRepository<UserWatchedLesson, Long> {
    List<UserWatchedLesson> findByUser(User user);

    @Query("SELECT l FROM Lession l JOIN UserWatchedLesson lw ON l.id = lw.lesson.id WHERE lw.user.id = :userId")
    List<Lession> findWatchedLessonsByUserId(@Param("userId") Long userId);

    boolean existsByLesson_IdAndUser_Id(Long lessonId, Long userId);

    Optional<UserWatchedLesson> findByLesson_IdAndUser_Id(Long lessonId, Long userId);

    List<UserWatchedLesson> findByUser_IdAndLesson_IdIn(Long userId, List<Long> lessonIds);

    @Query("""
    select uwl
    from UserWatchedLesson uwl
    where uwl.user.id = :userId and uwl.status = com.example.studyE.entity.ProgressStatus.DONE
    order by uwl.completedAt desc
""")
    List<UserWatchedLesson> findDoneHistory(@Param("userId") Long userId);


}
