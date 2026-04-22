export async function fetchJson<T>(url: string, init?: RequestInit): Promise<T> {
  try {
    const response = await fetch(url, init);

    if (!response.ok) {
      const message = await safeReadErrorMessage(response);
      throw new Error(
        `Request failed [${response.status} ${response.statusText}] ${url}${message ? `: ${message}` : ''}`,
      );
    }

    if (response.status === 204) {
      return undefined as T;
    }

    return (await response.json()) as T;
  } catch (error) {
    if (error instanceof Error) {
      if (error.name === 'TypeError') {
        throw new Error(`Network error calling ${url}. Check backend/proxy availability.`);
      }
      throw error;
    }
    throw new Error(`Unexpected error calling ${url}.`);
  }
}

async function safeReadErrorMessage(response: Response): Promise<string> {
  try {
    const text = await response.text();
    return text.trim();
  } catch {
    return '';
  }
}
