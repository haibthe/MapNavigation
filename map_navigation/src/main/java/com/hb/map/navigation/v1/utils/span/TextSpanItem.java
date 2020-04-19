package com.hb.map.navigation.v1.utils.span;

public class TextSpanItem implements SpanItem {

  private Object span;
  private String spanText;

  public TextSpanItem(Object span, String spanText) {
    this.span = span;
    this.spanText = spanText;
  }

  @Override
  public Object getSpan() {
    return span;
  }

  public String getSpanText() {
    return spanText;
  }
}
