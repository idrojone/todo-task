package me.idrojone.task_api.domain.exception;

import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.stereotype.Component;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import java.util.Map;
import graphql.schema.DataFetchingEnvironment;

@Component
public class GraphQlExceptionHandler extends DataFetcherExceptionResolverAdapter {
    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if (ex instanceof NotFoundException) {
            return GraphqlErrorBuilder.newError(env)
                .message(ex.getMessage())
                .extensions(Map.of("code", "NOT_FOUND"))
                .build();
        }
        return null;
    }
}
