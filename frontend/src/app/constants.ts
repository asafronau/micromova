export const enum ExamButtonRole {
  CHECK = 1,
  CONTINUE,
}

export function cleanString(s: string): string {
  s = s.trim();
  return s.replace(/[.,?!:;@#$%^&*()\/\[\]]/g, ' ').split(' ').filter(a => a).join(' ');
}

export function stringsEqual(s1: string, s2: string): boolean {
  s1 = cleanString(s1);
  s2 = cleanString(s2);
  return (s1.toUpperCase() === s2.toUpperCase()) || (s1.toLowerCase() === s2.toLowerCase());
}
