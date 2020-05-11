package com.envision.Staffing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.envision.Staffing.services.ShiftCalculatorTest;
import com.envision.Staffing.services.ShiftPlanningServiceTest;



@RunWith(Suite.class)
@SuiteClasses({ ShiftCalculatorTest.class,ShiftPlanningServiceTest.class })
public class AllTestsSuite {

}
