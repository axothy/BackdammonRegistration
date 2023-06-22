package ru.axothy.backdammon.registration.repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.axothy.backdammon.registration.model.Newbie;

@Repository
public interface NewbieRepository extends CrudRepository<Newbie, String> {}
