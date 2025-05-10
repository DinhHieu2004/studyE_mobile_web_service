package com.example.studyE.repository;


import com.example.studyE.Entity.Option;
import com.example.studyE.Entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {}

