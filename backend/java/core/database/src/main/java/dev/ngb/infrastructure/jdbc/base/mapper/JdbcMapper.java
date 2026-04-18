package dev.ngb.infrastructure.jdbc.base.mapper;

public interface JdbcMapper<D, J> {

    D toDomain(J entity);

    J toJdbc(D domain);
}

