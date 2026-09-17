package vn.elca.training.service;

import vn.elca.training.model.dto.GroupDto;

import java.util.List;

/**
 * Service interface for Group operations.
 *
 * @author nnnq
 */
public interface GroupService {

    List<GroupDto> findAll();
}
