package cinema.config

import java.io.File

import org.scalatest.FlatSpec

class LoadableConfigFileTest extends FlatSpec with LoadableConfig {

  "File config" should  "be loaded" in {
      val pathList = List("src", "test", "resources", "config")
      val path = new File(".").getCanonicalPath + pathList.fold("")(_ + File.separator +  _) + File.separator + "application.conf"
      assert(load(Some(path)).getString("test.data") == "TEST2")
  }
}