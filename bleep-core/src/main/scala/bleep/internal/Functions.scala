package bleep.internal

import bleep.{model, JsonList, JsonMap}

object Functions {
  def stripExtends(p: model.Project): model.Project =
    p.copy(
      `extends` = JsonList.empty,
      cross = JsonMap(p.cross.value.map { case (n, p) => (n, stripExtends(p)) }.filterNot { case (_, p) => p.isEmpty })
    )

}
