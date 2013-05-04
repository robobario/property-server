package model;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import javax.sql.rowset.Joinable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Joiner.on;
import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.ImmutableList.builder;
import static com.google.common.collect.Iterables.any;

public class PropertyReplacingContainer implements PropertyContainer {
    private static final Pattern EMBEDDED_PROPERTY_PATTERN = Pattern.compile("\\$\\{(.*?)\\}");
    private final PropertyContainer container;

    private PropertyReplacingContainer(PropertyContainer container) {
        this.container = container;
    }

    public static PropertyContainer decorate(PropertyContainer toDecorate) {
        return new PropertyReplacingContainer(toDecorate);
    }


    @Override
    public Map<String, String> getAllProperties() {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        for (String key : getAllPropertyKeys()) {
            builder.put(key, get(key));
        }
        return builder.build();
    }

    @Override
    public Set<String> getAllPropertyKeys() {
        return container.getAllPropertyKeys();
    }

    @Override
    public String get(String propertyKey) {
        String prop = container.get(propertyKey);
        if (prop != null) {
            prop = replaceEmbeddedProperties(prop);
        }
        return prop;
    }

    private String replaceEmbeddedProperties(String toReplace) {
        int depth = 0;
        List<Token> tokens = tokenize(toReplace);
        while (any(tokens, shouldContinue())) {
            if(depth > 10){
                throw new PropertyReplacementMaxDepthExceededException("attempted to replace more that ten properties deep");
            }
            tokens = replaceAllEmbeddedProps(tokens);
            depth++;
        }
        return on("").join(transform(tokens, toTokenValue()));
    }


    private List<Token> replaceAllEmbeddedProps(List<Token> tokens) {
        ImmutableList.Builder<Token> builder = builder();
        for(Token t : tokens){
            if(t.shouldContinue){
                builder.add(replaceProp(t));
            }else{
                builder.add(t);
            }
        }
        return builder.build();
    }

    private Token replaceProp(Token t) {
        Matcher matcher = EMBEDDED_PROPERTY_PATTERN.matcher(t.tokenValue);
        if(matcher.matches()){
            String replacedProperty = container.get(matcher.group(1));
            if(replacedProperty == null){
                return doNotContinueToken(t);
            }else{
                return continueToken(replacedProperty);
            }
        }else{
            return doNotContinueToken(t);
        }
    }

    private List<Token> tokenize(String toReplace) {
        ImmutableList.Builder<Token> builder = builder();
        int lastIndex = 0;
        Matcher matcher = EMBEDDED_PROPERTY_PATTERN.matcher(toReplace);
        while (matcher.find()) {
            builder.add(continueToken(toReplace.substring(lastIndex, matcher.start())));
            builder.add(continueToken(matcher.group(0)));
            lastIndex = matcher.end();
        }
        builder.add(continueToken(toReplace.substring(lastIndex)));
        return builder.build();
    }

    private Predicate<? super Token> shouldContinue() {
        return new Predicate<Token>() {
            @Override
            public boolean apply(Token input) {
                return input.shouldContinue;
            }
        };
    }


    private Function<? super Token,String> toTokenValue() {
        return new Function<Token, String>() {
            @Override
            public String apply(Token input) {
                return input.tokenValue;
            }
        };
    }


    @Override
    public void put(String propertyKey, String propertyValue) {
        container.put(propertyKey, propertyValue);
    }

    private static class Token{
        private boolean shouldContinue;
        private String tokenValue;

        public Token(boolean shouldContinue, String token) {
            this.shouldContinue = shouldContinue;
            this.tokenValue = token;
        }
    }

    private static Token continueToken(String token){
        return new Token(true, token);
    }

    private static Token doNotContinueToken(Token token){
        return new Token(false, token.tokenValue);
    }

}
