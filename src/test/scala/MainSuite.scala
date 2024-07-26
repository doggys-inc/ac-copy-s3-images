import cats.data.NonEmptyList
import munit.FunSuite

class MainSuite extends FunSuite:
  test("progress"):
    assertEquals(progress(1, NonEmptyList.fromListUnsafe(Array.fill(1000)(0).toList)), "0001/1000 (0%)")
  test("progress and percentage"):
    assertEquals(progress(21, NonEmptyList.fromListUnsafe(Array.fill(214)(0).toList)), "021/214 (9%)")
  test("progress and percentage"):
    assertEquals(progress(1, NonEmptyList.fromListUnsafe(Array.fill(1)(0).toList)), "1/1 (100%)")
  test("progress and percentage"):
    assertEquals(progress(1, NonEmptyList.fromListUnsafe(Array.fill(2)(0).toList)), "1/2 (50%)")
  test(""):
    assertEquals("123/124.jpg".takeWhile(_ != '/'), "123")
  test(""):
    assertEquals(List.empty.contains(true), false)
  test(""):
    assertEquals(None.contains(true), false)
