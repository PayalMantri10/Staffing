package com.envision.Staffing.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.envision.Staffing.model.JobDetails;
import com.envision.Staffing.repository.JobDetailsRepository;


public class JobDetailsService {

	@Autowired
	private JobDetailsRepository jobDetailsRepository;

	public List<JobDetails> getAllJobDetails() {
		List<JobDetails> jobDetailsList = (List<JobDetails>) jobDetailsRepository.findAll();

		if (jobDetailsList.size() > 0) {
			return jobDetailsList;
		} else {
			return new ArrayList<JobDetails>();
		}
	}

	public JobDetails getJobDetailsById(String id) {
		Optional<JobDetails> jobDetails = jobDetailsRepository.findById(id);

		if (jobDetails.isPresent()) {
			return jobDetails.get();
		} else {
			return null;
			// throw new RecordNotFoundException("No jobDetails record exist for given id");
		}
	}

	public JobDetails createOrUpdateJobDetails(JobDetails entity) {
		Optional<JobDetails> jobDetails = jobDetailsRepository.findById(entity.getId());

		if (jobDetails.isPresent()) {
			JobDetails newEntity = jobDetails.get();
			newEntity.setName(entity.getName());
			newEntity = jobDetailsRepository.save(newEntity);
			return newEntity;
		} else {
			entity = jobDetailsRepository.save(entity);
			return entity;
		}
	}

	public void deleteJobDetailsById(String id) {
		Optional<JobDetails> jobDetails = jobDetailsRepository.findById(id);

		if (jobDetails.isPresent()) {
			jobDetailsRepository.deleteById(id);
		} else {
			// throw new RecordNotFoundException("No jobDetails record exist for given id");
		}
	}

}