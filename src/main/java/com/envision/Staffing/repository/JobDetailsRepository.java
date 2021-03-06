package com.envision.Staffing.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.envision.Staffing.model.JobDetails;

@Repository
public interface JobDetailsRepository extends CrudRepository<JobDetails, String> {

	@Query("Select j from JobDetails j LEFT JOIN j.inputFtpDetails ifd LEFT JOIN j.outputFtpDetails ofD LEFT JOIN j.inputFileDetails iflD LEFT JOIN j.clinicians c  Where j.id=:id")
	JobDetails getByIdLeftJoin(@Param("id") String id);
	
	
	@Modifying(clearAutomatically = true)
	@Transactional
	@Query("delete from JobDetails j where j.id=:id")
	void deleteJobDetailById(@Param("id") String id);
	
}
