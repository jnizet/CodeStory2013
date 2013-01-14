package com.ninja_squad.jb.codestory.action;

import com.ninja_squad.jb.codestory.Action;
import com.ninja_squad.jb.codestory.HttpHeaders;
import com.ninja_squad.jb.codestory.HttpRequest;
import com.ninja_squad.jb.codestory.HttpResponse;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * Action used to parse and answer to non-URL-encoded arithmetic expression in the <code>q</code> parameter
 * @author JB
 */
public class ArithmeticAction implements Action {

    @Override
    public HttpResponse execute(HttpRequest request) throws UnsupportedEncodingException {
        String encodedExpression =
            request.getPathAndQueryString().substring(request.getPathAndQueryString().indexOf('=') + 1);
        // create a script engine manager
        ScriptEngineManager factory = new ScriptEngineManager();
        // create a JavaScript engine
        ScriptEngine engine = factory.getEngineByName("JavaScript");
        // evaluate JavaScript code from String
        try {
            Object result = engine.eval(encodedExpression);
            return HttpResponse.ok(result.toString());
        }
        catch (ScriptException e) {
            return HttpResponse.badRequest("Invalid expression: " + encodedExpression);
        }
    }
}
