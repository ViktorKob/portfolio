package net.thomas.portfolio.render;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.portfolio.render.format.text.HbaseIndexingModelTextRendererLibraryUnitTest;
import net.thomas.portfolio.render.service.RenderServiceControllerServiceAdaptorTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ HbaseIndexingModelTextRendererLibraryUnitTest.class, RenderServiceControllerServiceAdaptorTest.class })
public class RenderServiceTestSuite {
}