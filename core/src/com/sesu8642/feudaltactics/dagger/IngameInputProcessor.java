package com.sesu8642.feudaltactics.dagger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/** Binding annotation. **/
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface IngameInputProcessor {

}
