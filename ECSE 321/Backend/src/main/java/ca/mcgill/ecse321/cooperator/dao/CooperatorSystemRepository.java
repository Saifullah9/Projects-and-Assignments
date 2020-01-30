package ca.mcgill.ecse321.cooperator.dao;

import org.springframework.data.repository.CrudRepository;

import ca.mcgill.ecse321.cooperator.model.CooperatorSystem;

public interface CooperatorSystemRepository extends CrudRepository<CooperatorSystem, Integer> {

	
	CooperatorSystem findBySystemId(Integer systemId);

}
