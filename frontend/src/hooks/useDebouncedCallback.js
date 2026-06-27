import { useCallback, useEffect, useRef } from 'react'

/**
 * Returns a debounced version of `callback`, plus a `flush` function that runs any
 * pending call immediately (used when navigating away so the last keystrokes aren't lost).
 */
export const useDebouncedCallback = (callback, delayMs) => {
  const callbackRef = useRef(callback)
  const timeoutRef = useRef(null)
  const pendingArgsRef = useRef(null)

  callbackRef.current = callback

  useEffect(() => () => clearTimeout(timeoutRef.current), [])

  const debounced = useCallback((...args) => {
    pendingArgsRef.current = args
    clearTimeout(timeoutRef.current)
    timeoutRef.current = setTimeout(() => {
      pendingArgsRef.current = null
      callbackRef.current(...args)
    }, delayMs)
  }, [delayMs])

  const flush = useCallback(() => {
    if (pendingArgsRef.current === null) return
    clearTimeout(timeoutRef.current)
    const args = pendingArgsRef.current
    pendingArgsRef.current = null
    callbackRef.current(...args)
  }, [])

  return [debounced, flush]
}
