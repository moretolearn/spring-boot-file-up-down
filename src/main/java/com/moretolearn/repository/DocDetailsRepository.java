package com.moretolearn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moretolearn.model.DocDetails;

@Repository
public interface DocDetailsRepository extends JpaRepository<DocDetails, String> {

}
