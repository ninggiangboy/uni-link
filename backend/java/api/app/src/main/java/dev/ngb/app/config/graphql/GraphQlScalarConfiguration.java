package dev.ngb.app.config.graphql;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.scalars.ExtendedScalars;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import java.time.Instant;
import java.util.Locale;

@Configuration
public class GraphQlScalarConfiguration {

    @Bean
    public RuntimeWiringConfigurer graphqlScalars() {
        return wiringBuilder -> wiringBuilder
                .scalar(ExtendedScalars.GraphQLLong)
                .scalar(instantScalar());
    }

    private static GraphQLScalarType instantScalar() {
        return GraphQLScalarType.newScalar()
                .name("Instant")
                .description("UTC instant as ISO-8601 text")
                .coercing(new Coercing<Instant, String>() {
                    @Override
                    public String serialize(Object dataFetcherResult, GraphQLContext context, Locale locale)
                            throws CoercingSerializeException {
                        if (!(dataFetcherResult instanceof Instant instant)) {
                            throw new CoercingSerializeException("Expected an Instant");
                        }
                        return instant.toString();
                    }

                    @Override
                    public Instant parseValue(Object input, GraphQLContext context, Locale locale)
                            throws CoercingParseValueException {
                        try {
                            return Instant.parse(input.toString());
                        } catch (Exception e) {
                            throw new CoercingParseValueException("Invalid Instant", e);
                        }
                    }

                    @Override
                    public Instant parseLiteral(Value<?> input, CoercedVariables variables, GraphQLContext context, Locale locale)
                            throws CoercingParseLiteralException {
                        if (input instanceof StringValue sv) {
                            try {
                                return Instant.parse(sv.getValue());
                            } catch (Exception e) {
                                throw new CoercingParseLiteralException("Invalid Instant literal", e);
                            }
                        }
                        throw new CoercingParseLiteralException("Expected string literal for Instant");
                    }
                })
                .build();
    }
}
