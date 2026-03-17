package com.javadev.bod.painless.request.query.ast;

import com.javadev.bod.painless.request.PathRef;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ExistsQueryNode implements QueryNode {

    private final PathRef field;

}
