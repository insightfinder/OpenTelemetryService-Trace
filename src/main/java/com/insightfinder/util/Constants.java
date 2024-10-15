package com.insightfinder.util;

import com.insightfinder.model.ContextMetadata;
import io.grpc.Context;

public class Constants {

  public static final Context.Key<ContextMetadata> METADATA_KEY = Context.key("metadata");
}
