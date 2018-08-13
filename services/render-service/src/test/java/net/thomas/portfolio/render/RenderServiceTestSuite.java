package net.thomas.portfolio.render;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.thomas.portfolio.render.common.context.ContextTestSuite;
import net.thomas.portfolio.render.format.simple_rep.HbaseIndexingModelSimpleRepresentationRendererLibraryUnitTest;
import net.thomas.portfolio.render.format.text.HbaseIndexingModelTextRendererLibraryUnitTest;
import net.thomas.portfolio.render.service.RenderServiceControllerServiceAdaptorTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ ContextTestSuite.class, HbaseIndexingModelTextRendererLibraryUnitTest.class,
		HbaseIndexingModelSimpleRepresentationRendererLibraryUnitTest.class, RenderServiceControllerServiceAdaptorTest.class })
public class RenderServiceTestSuite {
}