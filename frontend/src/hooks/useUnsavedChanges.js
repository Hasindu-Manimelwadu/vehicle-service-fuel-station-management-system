import { useCallback, useEffect } from "react";

export default function useUnsavedChanges(isDirty) {
  useEffect(() => {
    const handleBeforeUnload = (event) => {
      if (!isDirty) return;
      event.preventDefault();
      event.returnValue = "";
    };

    window.addEventListener("beforeunload", handleBeforeUnload);
    return () => window.removeEventListener("beforeunload", handleBeforeUnload);
  }, [isDirty]);

  return useCallback(
    (action) => {
      if (!isDirty || window.confirm("You have unsaved changes. Leave this page and discard them?")) {
        action();
        return true;
      }
      return false;
    },
    [isDirty]
  );
}
