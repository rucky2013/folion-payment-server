package com.folionmedia.payment.server.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.folionmedia.payment.server.domain.Membership;

@Repository
public interface MembershipRepository extends CrudRepository<Membership, String> {

}
