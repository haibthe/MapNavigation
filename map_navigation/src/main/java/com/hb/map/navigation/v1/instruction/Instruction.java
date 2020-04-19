package com.hb.map.navigation.v1.instruction;


import com.hb.map.navigation.v1.routeprogress.RouteProgress;

/**
 * Base Instruction. Subclassed to provide concrete instructions.
 *
 * @since 0.4.0
 */
public abstract class Instruction {

  /**
   * Will provide an instruction based on your specifications
   *
   * @return {@link String} instruction that will be voiced on the client
   * @since 0.4.0
   */
  public abstract String buildInstruction(RouteProgress routeProgress);
}
