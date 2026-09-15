package com.lendmesh.api.repository;

import com.lendmesh.api.domain.BorrowerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowerProfileRepository extends JpaRepository<BorrowerProfile, String> {
}
