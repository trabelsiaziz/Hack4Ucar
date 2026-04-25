export interface NavItem {
  id: string;
  label: string;
  icon: string;
  route: string;
  order: number;
  visible: boolean;
  children: NavItem[];
}
